//
//  connectViewController.h
//  connect
//
//  Created by Michele Sciabarra on 26/02/2011.
//  Copyright we.iphonize.it 2011. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface connectViewController : UIViewController<UITableViewDelegate, UITableViewDataSource> {
	
	UITextView* textView;
	UITextField* textField;
	UITableView* tableView;
	NSMutableData *webData;
	NSArray *jsonData;
	NSDictionary *jsonDict;
}

-(IBAction) loadUrl:(id)sender;


-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response;
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data;
-(void)connectionDidFinishLoading:(NSURLConnection *)connection;

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView;
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section;
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;


@property (nonatomic, retain) IBOutlet UITextView* textView;
@property (nonatomic, retain) IBOutlet UITextField* textField;
@property (nonatomic, retain) IBOutlet UITableView* tableView;

@end

