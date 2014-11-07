#Filechooser for Android

Simple file chooser android library compatible with API 9+.
Demo app requires appcompat v21.

###Features
 - single/multi file selection
 - single/multi directory selection
 - single/multi file and directory selection at the same time
 - use of file observer(reloads when content of current directory change)
 - details file information dialog at long click

![](https://raw.github.com/lecho/filechooser/master/screen.jpg)

### Usage

```java
//Start FilechooserActivity for result
public void someMethod(){
    Intent intent = new Intent(getActivity(), FilechooserActivity.class);
    intent.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE);
    intent.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM);
    startActivityForResult(intent, requestCode);
}

//Handle onActivityResult
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
        ArrayList<String> paths = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);
    }
}
```

# License

    Copyright (C) 2012 Leszek Wach

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
