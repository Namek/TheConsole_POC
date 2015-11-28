## The Console

### What?

Wide-usage JavaScript-able Console for Windows OS, currently looking and animating like the one in Quake 3 Arena game. Currently made with libgdx and Java 8.

Features:
* custom scripts
* aliasing commands
* auto-reloading of scripts when script file is modified
* command invoke history like in bash
* one-line JavaScript code runnable directly in shell (do some math or whatever)
* JSON configuration

### How to use?

1. Copy `core/assets/scripts` into  `%APPDATA%/TheConsole/scripts`
2. Import as Gradle project, run `the-console-desktop`
3. When run, toggle console visibility by global hotkey: `CTRL + ~`
4. Type `help` and hit `ENTER`
5. Have fun by exploring/editing content of scripts and even adding more of them.
6. You can exit hitting `ALT + F4`

## APIs

### Nashorn (Java 8 extension)

https://gist.github.com/WebReflection/9627010

https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/api.html


### Globals variables and functions

```js
args.length
args[0], args[1], ...
Utils.audioFilePlayer.play(filePath)
console.log(text)
console.log(text, color)
console.error(text)
console.clear()
console.hide()
console.window
assert(bool, string)
assertInfo(bool, string)
JavaClass(className) // Java: Class.forName(className);
System // Java: System.class
```

### Script Storage

Every script can store it's configuration or whatever. To see some examples go see `filesystem`.

1. Get your storage:
    ```js
    Storage = Storage.getGlobalStorage("myscript")
    ```

2. Get variable
    ```
    var path = Storage.get("path")
    console.log(path)
    ```

3. Overwrite variable and save Storage
    ```js
    var newPath = ...
    Storage.set("path", newPath)
    Storage.save()
    ```

### Assertion

You can `assert` whatever you like, e.g. script arguments.

When first argument of `assert`/`assertInfo` is not true, it stops whole scripts and displays given text to the console:

1. red text:
    ```js
    assert(args.length == 0, "Please, no args.")
    ```

2. white text:
    ```js
    assertInfo(args.length == 0, "Please, no args.")
    ```