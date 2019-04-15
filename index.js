import { NativeModules } from 'react-native';
const { RNImageAlbum } = NativeModules;

export default class ImageAlbum {

    static saveToAlbum(url,callback) {
        //保存图片至相册
        RNImageAlbum.saveToAlbum(url,callback);
    }
};
