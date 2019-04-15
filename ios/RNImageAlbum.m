
#import "RNImageAlbum.h"
#import <UIKit/UIKit.h>

@interface RNImageAlbum ()
@property (nonatomic,copy) RCTResponseSenderBlock successCallBack;
@end

@implementation RNImageAlbum

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(saveToAlbum:(NSString *)imageUrl callback:(RCTResponseSenderBlock)callback) {
    self.successCallBack = callback;
    if ([self validateImageUrl:imageUrl]) {
        dispatch_queue_t ioQueue = dispatch_queue_create("com.image.download", DISPATCH_QUEUE_SERIAL);
        dispatch_async(ioQueue, ^{
            NSURL *imageURL = [NSURL URLWithString:imageUrl];
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
- (BOOL)validateImageUrl:(NSString *)imageUrl {
    if (!imageUrl || imageUrl.length == 0) {
        if (self.successCallBack) {
            self.successCallBack(@[@"imageUrl参数为空"]);
        }
        return NO;
    }
    return YES;
}

- (void)imageSavedToPhotosAlbum:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if (self.successCallBack) {
        self.successCallBack(@[!error ? @"成功保存图片到相册" : error.localizedDescription]);
    }
}

@end
  
