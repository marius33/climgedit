# climgedit

CLImgEdit can be used in 3 different ways:

1. as a library:
    you can import climgedit and use the Image class to edit images
        //TODO javadoc
2. as a runnable jar from the command-line
    you can pass arguments directly to the jar and have it do operations on the image:
        -i specifies the input file
        -o specifies the output file
        -h prints help
        -c crops the image
            -c startX startY Width Height
            -c Width Height (which is equivalent to passing 0 to startX and startY)
        -r resizes the image
            -r Width Height [mode] (mode is optional, choose between "exact", "crop" or "pad". if ommited, defaults to "exact")
        -x rotates the image counterclockwise
            -x Angles [mode] (mode is optional, choose between "crop", "pad", "pad-keep-size". if ommited, defaults to "crop")
        -p pads the image
            -p Thickness
            -p Thickness Color(written in hex format, starting with "0x")
            -p LeftRight TopBottom (pads with different thickness the sides or top and bottom)
            -p LeftRight TopBottom Color
            -p Left Right Top Bottom
            -p Left Right Top Bottom Color
        -s swaps colors
            -s ColorToBeReplaces ColorToBeReplacesWith ColorRange AlphaRange (the last 2 arguments define the strictness of the                               replacement, the smaller the value, the stricter the check. 
                    A value of zero specifies that only the exact color be replaces, while a value of 255 basically means every color.                         Alpha range is the same but is meant for transparency)
3. with the GUI
    running the jar with no input argument automatically starts the GUI, while running the jar with no output argument automatically starts the GUI but with the input image already modified by the operations. This is not intended to be used on it's own, but more as a way to see what exactly the operations do and modify accordingly the script.
    
 
