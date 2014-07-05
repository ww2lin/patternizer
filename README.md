patternizer
===========

create a program to create simple flower-like pattern drawing using direct manipulation undo,redo, 256 -rgb colors, export the pattern, scaling and rotating the pattern.


-  create simple flower-like pattern drawing using direct manipulation

draw draging from the center cricle, the user is allow to draw shapes,
which then can be raotated or scaled to produce flower-likae patterns.

if the current angle is bigger than 180 degrees then only one instance of the object will be drawn.


****note****
the undo redo is caching everything, which might take up alot of memory (for virutal box)
the default "make run" includes undo/redo

type the following line below in the console to disable undo and redo
make nohistory 


Enhancements:
	
	rotation - the rotation of the shape can be rotated both ways

	inverse - if the user try to scale from bottom up and pass the center
		   the shaped will be flip to the other side(just like if the usuer was rotation to the right from the top of the screen)
	
	Undo/Redo buttons - the player is allow to undo their drawings,
			    if so the redo button is enabled. 
			    only cache new shapes and colors changes, does not
			    cache change in stroke and rotation(which can be done, but requires much more memory)

	thickness - drag the slider to change the stroke thickness
		    also the color background for the label shows the next color in sequence
		    the user can also select on a existence shape and adjust the size.
		    stroke value label will be updated as requried.

	custom color - opens a pop up and allows user to change to a color of their choice using RGB
			the priority as follow:
			background (checkbox for background is ticked)
			shape (only if it is selected/highlighted)
			center circle (if background not ticked shape is not selected)

	delete - if a shape is selected the user can delete the shape by right cliking with the mouse.

	exporting - when clicking on save image, the currently display will be
	            saved,this function will save the highlight as well if you have
		    any shapes selected.
