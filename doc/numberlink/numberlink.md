Number link
===========

`Problem.bind()` returns resolved map.

![resolved problem](numberlink.svg)

a|b
---|---
あああ|いいいいいいいいいいいいいいいいいい
あああ|いいいいいいいいいいいいいいいいいい
あああ|いいいいいいいいいいいいいいいいいい
あああ|いいいいいいいいいいいいいいいいいい
あああ|いいいいいいいいいいいいいい<br>うううううううううううううううう<br>えええええええええええ

You can create tables by assembling a list of words and dividing them with hyphens - (for the first row),
and then separating each column with a pipe |:

First Header  | Second Header
------------- | -------------
Content Cell  | Content Cell
Content Cell  | Content Cell

You can also include inline Markdown such as links, bold, italics, or strikethrough:

| Name | Description          |
| ------------- | ----------- |
| Help      | ~~Display the~~ help window.|
| Close     | _Closes_ a window     |
Finally, by including colons : within the header row,
you can define text to be left-aligned, right-aligned,
or center-aligned:

| Left-Aligned  | Center Aligned  | Right Aligned |
| :------------ |:---------------:| -----:|
| col 3 is      | some wordy text | $1600 |
| col 2 is      | centered        |   $12 |
| zebra stripes | are neat        |    $1 |

A colon on the left-most side indicates a left-aligned column;
a colon on the right-most side indicates a right-aligned column;
a colon on both sides indicates a center-aligned column.