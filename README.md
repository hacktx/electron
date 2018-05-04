![Electron](electron.png)

[![Software License](https://img.shields.io/badge/license-EPL-brightgreen.svg)](LICENSE)

Hackathon check-in system backed by [Nucleus](https://github.com/hacktx/nucleus).

## Discontinued since HackTX 2017
HackTX check-in is now powered by [Cumin](https://github.com/hacktx/cumin)!

## Setup
Clone this repository into a directory of your choice and open using [Android Studio](https://developer.android.com/sdk/index.html). Electron requires the latest Android SDK and support libraries, along with Google Play Services. Use SDK Manager to get these dependencies and gradle should take care of the rest.

Edit `electron.props` so it points to the API root of your Nucleus instance. For more help installing Nucleus, see [here](https://github.com/hacktx/nucleus/blob/master/README.md).

To compile, just run `gradlew build`.

## Usage
### QR codes
Electron primarily uses QR codes to get an attendee's information. The QR code is simply the attendee's email address in plaintext. For examples, see the HackTX [Android](https://github.com/hacktx/android) and [iOS](https://github.com/hacktx/iOS-HackTX-2015) applications.

In the event that a QR code is not available, an attendee's email address can be manually entered.
