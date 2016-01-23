# Interface: PeterInterface
## Functions:

```java
+ Constructor(ImageParams params);
```

Constructor to set the image processing parameters.

```java
+ ImageOutputSet process(ImageInputSet input);
```

process takes an input of 5 images, processes them based on internally
set parameters and gives back a set of 4 images in an ImageOutputSet
class. This function blocks until the result is given.


## Helper Classes:

### ImageParams
Yet to be fully decided, but simple parameters such as blur amount

### ImageOutputSet
```java
+ Image frontImage;
+ Image leftWindow;
+ Image rightWindow;
+ Image backWindow
```

### ImageInputSet
```java
+ Image frontImageLeft;
+ Image frontImageRight;
+ Image leftWindow;
+ Image rightWindow;
+ Image backWindow
```