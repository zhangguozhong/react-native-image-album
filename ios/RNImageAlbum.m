
#import "RNImageAlbum.h"
#import <UIKit/UIKit.h>

@interface RNImageAlbum ()

@property (nonatomic,copy) RCTResponseSenderBlock doCallBack;

@end

@implementation RNImageAlbum

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()


RCT_EXPORT_METHOD(saveToAlbum:(NSString *)url callback:(RCTResponseSenderBlock)callback) {
    self.doCallBack = callback;
    if ([self validateUrl:url]) {
        dispatch_queue_t ioQueue = dispatch_queue_create("com.image.download", DISPATCH_QUEUE_SERIAL);
        dispatch_async(ioQueue, ^{
            NSURL *imageURL = [NSURL URLWithString:url];
            NSData *imageData = [NSData dataWithContentsOfURL:imageURL];
            
            if (imageData) {
                UIImage *downloadImage = [UIImage imageWithData:imageData];
                dispatch_async(dispatch_get_main_queue(), ^{
                    UIImageWriteToSavedPhotosAlbum(downloadImage, self, @selector(imageSavedToPhotosAlbum:didFinishSavingWithError:contextInfo:), nil);
                });
            }
        });
    }
}

//验证传入的imageUrl是否合法
- (BOOL)validateUrl:(NSString *)url {
    if (!url || url.length == 0) {
        if (self.doCallBack) {
            self.doCallBack(@[@"url参数为空"]);
        }
        return NO;
    }
    return YES;
}

- (void)imageSavedToPhotosAlbum:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if (self.doCallBack) {
        self.doCallBack(@[!error ? @"成功保存图片到相册" : error.localizedDescription]);
    }
}

@end
  
