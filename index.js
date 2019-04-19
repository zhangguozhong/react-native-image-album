import { NativeModules } from 'react-native';
const { RNImageAlbum } = NativeModules;

const ImageAlbum = {
    //保存图片
    saveImageWithUrl:function (url,callback) {
        if (!url) {
            return;
        }

        RNImageAlbum.saveImageWithUrl(url, callback);
    }
};

export default ImageAlbum;

