
# react-native-image-album

## Getting started

`$ npm install react-native-image-album --save`

### Mostly automatic installation

`$ react-native link react-native-image-album`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-image-album` and add `RNImageAlbum.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNImageAlbum.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.library.photo.RNImageAlbumPackage;` to the imports at the top of the file
  - Add `new RNImageAlbumPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-image-album'
  	project(':react-native-image-album').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-image-album/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-image-album')
  	```


## Usage
```javascript
import RNImageAlbum from 'react-native-image-album';

// TODO: What to do with the module?
RNImageAlbum;
```
  