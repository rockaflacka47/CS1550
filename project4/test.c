static int cs1550_read(const char *path, char *buf, size_t size, off_t offset,
   struct fuse_file_info *fi)
{
    (void) fi;

    // check to make sure path exists
    // check that size is > 0
    // check that offset is <= to the file size
    // read in data
    // set size and return, or error

    char dir[MAX_LENGTH];           // directory
    char filename[MAX_LENGTH];      // filename
    char ext[MAX_LENGTH];           // extension

    // break path up into directory, filename, and extension
    int scan_result = sscanf(path, "/%[^/]/%[^.].%s", dir, filename, ext);

    // make sure size and offset are valid
    if ((size == 0) ||
        (offset > size)) {
        return -EFBIG;                                                              // ERROR: offset is beyond file size

    } else if (scan_result < 2) {
        return -EISDIR;                                                             // ERROR: trying to read out a directory

    } else {
        // check to make sure path (file) exists by getting the file
        long dir_block = find_directory(dir);                                       // get block offset to where this dir entry is held
        cs1550_directory_entry *dir_entry = get_directory(dir_block);               // get the actual dir entry struct
        cs1550_file_directory *file_entry = get_file(dir_entry, filename, ext);     // get the filename struct
        long block_loc = file_entry->nStartBlock;                                   // store location, on disk, of block

        // determine how many blocks will be needed to traverse
        long num_blocks_needed = (1 + (((size - 1) / MAX_DATA_IN_BLOCK)));          // get ceiling value of blocks needed
        long start_block = offset / MAX_DATA_IN_BLOCK;                              // the block to start writing/appending to
        long start_offset = offset % MAX_DATA_IN_BLOCK;                             // specific offset within starting block
        size_t bytes_read = 0;                                                      // number of bytes written so far
        
        // read data into buf from disk
        cs1550_disk_block *disk_block = get_disk_block(block_loc, start_block);     // get data block
        long block_num, data_offset=0;
        size_t bytes_left;                                                          // amount of bytes left to write out
        for (block_num = 0; block_num < num_blocks_needed; block_num++) {
            bytes_left = size - bytes_read;                                         // calculate amount of bytes left to write
            data_offset = ((block_num == 0) ? start_offset : 0);                    // if first block, start at proper offset

            // copy over # bytes_left or MAX_DATA_IN_BLOCK to this block
            strncpy((buf + bytes_read), (disk_block->data + data_offset), ((bytes_left < MAX_DATA_IN_BLOCK) ? bytes_left : MAX_DATA_IN_BLOCK));
            bytes_read += ((bytes_left < MAX_DATA_IN_BLOCK) ? bytes_left : MAX_DATA_IN_BLOCK);

            // grab another free block if needed
            if (block_num != (num_blocks_needed - 1)) {                             // -1 to check against 0-based
                block_loc = find_free_block();                                      // get free block for next write
                if (block_loc < 0) { return -ENOSPC; }                              // ERROR: no space left
                disk_block->nNextBlock = block_loc;                                 // store location to next block
            } else {
                disk_block->nNextBlock = 0;                                         // store location to next block   
            }

            // switch to the next block
            if (block_num != (num_blocks_needed - 1)) {
                disk_block = get_disk_block(block_loc, block_num+1);                // +1 to get NEXT block number
            }
        }
    }

    return size;
}