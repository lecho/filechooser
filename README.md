#Filechooser
===========

File chooser library for android, api 8+.

![screenshot1](https://raw.github.com/lecho/filechooser/master/screen_1.png)

## Usage:
    private static final int REQUEST_CODE = 1;

    @Override
    public void foo() {
        Intent intent = new Intent(context, FileChooserActivity.class);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CODE:	
            if (resultCode == Activity.RESULT_OK) {	
                Uri uri = data.getData();
            }
        }
    }

## License

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
