# gifRender

Welcome, you who wish to bear the mantle of the **Render of GIFs**!

### What does gifRender do? 

_gifRender_ is a simple GIF manipulator. It allows the user to create GIFs from a series of images,
while specifying parameters such as frame rate and image size. One may also 'rend' existing GIFs, splitting
them up into their component images for easy frame extraction. Additional features may include GIF editing and
file conversions. Let _gifRender_ fulfill all your gif needs!

### Who is gifRender for?

_gifRender_ avails itself to those who desire to create, rend, or otherwise manipulate GIFs.
Have a GIF that's just a tad too slow or fast? Like one particular frame of a GIF, but not the rest?
Want to make a GIF slideshow out of your family pictures?
Look no further than _gifRender_!

### Why gifRender?

I decided to create _gifRender_ as a natural continuation of [my previous project](https://github.com/VictorVy/myGallery),
a media gallery program equipped with a tagging system and search functionality. One of the planned features for this
project was a simple image, GIF, and/or video editing tool. It ended up falling outside the scope of the project,
so I decided this would be a good opportunity to create a new project that would focus on this feature, starting with GIFs.

### User stories

- As a user, I want to be able to add an arbitrary number of images to a list called a roster
- As a user, I want to be able to remove images from my roster
- As a user, I want to be able to specify parameters such as frame rate for each or all of the images in my roster
- As a user, I want to be able to view the items in my roster, along with their details
- As a user, I want to be able to output a GIF comprised of the images in my roster
- As a user, I want to be able to rend a GIF into its component images, arranged in my roster
- As a user, I want to be able to download one or more individual items from my roster
- As a user, I want to be able to save the state of my roster for later
- As a user, I want to be able to load my previously saved roster

### Instructions for Grader

- You can generate both required actions related to adding Xs to a Y by using the application normally
  - Load images/GIFs to the roster by pressing the "Add" button, or File &#8594; Add (hotkey: alt+f &#8594; a), or by dragging and dropping images onto the roster.
  - Perform various operations (shift, download, delay, etc.) on the roster items by selecting the desired operation from the dropdown, entering indices into the text field, and pressing the button.
  - Output the roster as a GIF by pressing the "Output" button, or File &#8594; Output (hotkey: alt+f &#8594; o).
- You can locate the visual components when you add files to the roster; you will see images displayed in the roster
- You can save the state of my application by going to File &#8594; Save (hotkey: alt+f &#8594; s)
- You can load the state of my application by going to File &#8594; Load (hotkey: alt+f &#8594; l)

### Phase 4: Task 2

Sun Mar 26 22:52:09 PDT 2023<br>
Roster created<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_0.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_1.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_2.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_3.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_4.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_5.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_6.png to the roster<br>
Sun Mar 26 22:53:20 PDT 2023<br>
Added nyan_7.png to the roster<br>
Sun Mar 26 22:53:59 PDT 2023<br>
Removed nyan_2.png from the roster<br>
Sun Mar 26 22:53:59 PDT 2023<br>
Removed nyan_3.png from the roster<br>
Sun Mar 26 22:54:34 PDT 2023<br>
Swapped nyan_7.png and nyan_0.png<br>
Sun Mar 26 22:54:45 PDT 2023<br>
Shifted nyan_1.png from index 1 to index 2<br>
Sun Mar 26 22:55:00 PDT 2023<br>
Renamed nyan_7.png to nyarn<br>
Sun Mar 26 22:55:35 PDT 2023<br>
Set delay of nyan_1.png to 20

### Phase 4: Task 3

First, the most obvious refactoring I would do is to delegate many of the responsibilities handled by the
GifRenderApp class to other classes. For example, I might add a class to handle file opening/saving, and a class to
handle alerts and dialogs. This would help the cohesion of my code. Additionally, I would also implement custom
exceptions. This would not change the functionality of my program, but it would make it easier to debug and maintain.
If I had more time, I would also try to use the Observer design pattern for my model, reducing repetition elsewhere in
the code and making the programmer's life easier.