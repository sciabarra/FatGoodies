//
//  connectAppDelegate.h
//  connect
//
//  Created by Michele Sciabarra on 26/02/2011.
//  Copyright we.iphonize.it 2011. All rights reserved.
//

#import <UIKit/UIKit.h>

@class connectViewController;

@interface connectAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    connectViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet connectViewController *viewController;

@end

