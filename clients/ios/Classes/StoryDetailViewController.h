//
//  StoryDetailViewController.h
//  NewsBlur
//
//  Created by Samuel Clay on 6/24/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "BaseViewController.h"

@class NewsBlurAppDelegate;
@class ASIHTTPRequest;

@interface StoryDetailViewController : BaseViewController
<UIScrollViewDelegate, UIGestureRecognizerDelegate,
UIActionSheetDelegate> {
    NewsBlurAppDelegate *appDelegate;
    
    NSString *activeStoryId;
    NSMutableDictionary *activeStory;
    UIView *innerView;
    UIWebView *webView;
    NSInteger pageIndex;
    BOOL pullingScrollview;
    BOOL inTextView;
    BOOL inDoubleTap;
    NSURL *activeLongPressUrl;
    NSInteger actionSheetViewImageIndex;
    NSInteger actionSheetCopyImageIndex;
    NSInteger actionSheetSaveImageIndex;
}

@property (nonatomic) IBOutlet NewsBlurAppDelegate *appDelegate;
@property (nonatomic) NSString *activeStoryId;
@property (nonatomic, readwrite) NSMutableDictionary *activeStory;
@property (nonatomic) IBOutlet UIView *innerView;
@property (nonatomic) IBOutlet UIWebView *webView;
@property (nonatomic) IBOutlet UIView *feedTitleGradient;
@property (nonatomic) IBOutlet UIView *noStoryMessage;
@property (nonatomic, assign) BOOL pullingScrollview;
@property (nonatomic, assign) BOOL inTextView;
@property (nonatomic, assign) BOOL isRecentlyUnread;

@property NSInteger pageIndex;
@property (nonatomic) MBProgressHUD *storyHUD;

- (void)initStory;
- (void)hideNoStoryMessage;
- (void)drawStory;
- (void)drawStory:(BOOL)force withOrientation:(UIInterfaceOrientation)orientation;
- (void)showStory;
- (void)clearStory;
- (void)hideStory;

- (void)toggleLikeComment:(BOOL)likeComment;
- (void)flashCheckmarkHud:(NSString *)messageType;
- (void)scrolltoComment;
- (void)changeWebViewWidth;
- (void)showUserProfile:(NSString *)userId xCoordinate:(int)x yCoordinate:(int)y width:(int)width height:(int)height;
- (void)checkTryFeedStory;
- (void)setFontStyle:(NSString *)fontStyle;
- (void)changeFontSize:(NSString *)fontSize;
- (void)changeLineSpacing:(NSString *)lineSpacing;
- (void)refreshComments:(NSString *)replyId;

- (void)openShareDialog;
- (void)openTrainingDialog:(int)x yCoordinate:(int)y width:(int)width height:(int)height;
- (void)finishLikeComment:(ASIHTTPRequest *)request;
- (void)subscribeToBlurblog;
- (void)finishSubscribeToBlurblog:(ASIHTTPRequest *)request;
- (void)requestFailed:(ASIHTTPRequest *)request;
- (void)setActiveStoryAtIndex:(NSInteger)activeStoryIndex;
- (NSString *)getHeader;
- (NSString *)getShareBar;
- (NSString *)getComments;
- (NSString *)getComment:(NSDictionary *)commentDict;
- (NSString *)getReplies:(NSArray *)replies forUserId:(NSString *)commentUserId;
- (NSString *)getAvatars:(NSString *)key;
- (void)refreshHeader;
- (void)refreshSideoptions;

- (CGPoint)pointForGesture:(UIGestureRecognizer *)gestureRecognizer;

- (void)fetchTextView;
- (void)finishFetchTextView:(ASIHTTPRequest *)request;
- (void)requestFailed:(ASIHTTPRequest *)request;


@end
