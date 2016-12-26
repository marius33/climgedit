# climgedit
Simple implementation of the imgscalr library in a runnable jar, which can execute a sequence of operations on an image entirely through the command line.

This is supposed to be a simple runnable .jar which can be used to apply simple operations on an image. Most of the operations are implemented using the imgScalr library.
The jar requires an input and an output file and then a sequence of operations can be passed to it. They will be executed in the order given and more of the same operation can be chained.

Currently supported operations are:
  crop, resize, pad, replace colours.
The crop, resize and pad operations are almost directly linked to the functions of the imgScalr library.
The colour replacement function is still very simple and will just change the colours, with not many options.

Operation description:
  -c, --crop: this operation requires 3 parameters given. A starting point on the "x" axis, one on the "y" axis and the size of the are to be cropped.
              e.g. -c 50 40 250x150 will return the part of the input image starting at coordinates (x, y) = (50, 40) with a size of 250 pixels width, and 150 pixels height.
  -r, --resize: resizes the image to the specified size. Passing just the new size will assume "automatic" ratio adjustment, while passing a second argument will set the mode.
              e.g. -r 500x500 is equivalent to -r 500x500 auto, but mode can be changed to the same modes described in the imgScalr library.
  -p, --pad: pads the image with a border of specified thickness, equal on all sides
              e.g. -p 50 will pad with a 50 pixels thick black border on all sides
  -s, --swap-colours: replaces every pixel that contains the first colour with the second colour
              e.g. -s 0xFF000000 0x7FFF0000 will replace every black pixel with 100% alpha to a red pixel with 50% alpha. The format is ARGB with 1 byte for each channel

Chaining multiple operations:
  Multiple operations can be chained together.
  e.g. Assume the file "in.png" is a random image of size 500x500
  We can turn this image into a 1920x1080 size jpg image with red left and right margins using:
    java -jar imgEdit.jar -i in.png -o out.jpg -r 1920x1080 -p 420 -c 0 420 1920x1080 -s 0xFF000000 0xFFFF0000
    
  In this example the first operation will resize the image to a 1080x1080 picture, because auto mode will keep the ratio.
  Second operation will add a border of width 420 on all sides, turning the picture into a 1920x1920 one.
  Third operation will crop the image to the actual 1920x1080 size, cutting just the upper and lower padding.
  Fourth operation will change the black border added to a solid red one (however if the image also had other solid black pixels, they will be changed also.
 
