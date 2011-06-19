//
//  connectViewController.m
//  connect
//
//  Created by Michele Sciabarra on 26/02/2011.
//  Copyright we.iphonize.it 2011. All rights reserved.
//

#import "connectViewController.h"
#import "CJSONDeserializer.h" 

@implementation connectViewController

@synthesize textView;
@synthesize textField;
@synthesize tableView;


-(IBAction) loadUrl:(id)sender {
	
	NSLog(@"loadUrl");
	
	NSURL *url = [NSURL URLWithString: [textField text]];
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc]  initWithURL:url];
	NSURLConnection *connection = 
	  [NSURLConnection connectionWithRequest:[request autorelease] delegate:self];
	if(connection) {
		webData = [[NSMutableData data] retain];
	} else {
		NSLog(@"connection failed");
	}

}

-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {

	//NSLog(@"%@ %@",[response MIMEType], [response textEncodingName]);	
	[webData setLength: 0];

}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {

	NSLog(@"len: %d",[data length]);
	[webData appendData:data];
	//NSLog(@"data: %s",[webData bytes]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	
	//NSString *json = [[NSString alloc] initWithCString:[webData bytes] encoding:NSUTF8StringEncoding];
	NSError *error = nil;
	NSDictionary *dictionary = 
	[[CJSONDeserializer deserializer] 
	 deserializeAsDictionary:webData error:&error];
	
	
	if(!error) {
		
		//NSLog(@"connectionDidFinishLoading: %@", dictionary);
		//[tableView reloadData];
		//for(NSObject* o in dictionary)
		//	NSLog(@"%@\n",o);
		NSArray* children = [dictionary objectForKey:@"items"];
		for(NSDictionary* d in children) {
			NSObject *o = [d objectForKey:@"description"]; 
			NSLog(@">>> %@: %@", o, [o class] );
		}
		jsonData = [[dictionary objectForKey: @"items"] retain];
		[tableView reloadData];
	} else {
		NSLog(@"cannot parse");
	}

	//[textView setText:json]; 
}



// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
	jsonData = [[[NSArray alloc] init] autorelease];
    [super viewDidLoad];
}


- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}

- (void)dealloc {	
    [super dealloc];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	//NSLog(@"%@", jsonData);		
	return  [jsonData count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
	}
	
	//cell.textLabel.text = [list objectAtIndex: [indexPath row]];
	int i = [indexPath row];
	NSDictionary* d = [jsonData objectAtIndex:i];
	//NSString* text = [d objectForKey:@"description"]; 
	NSString* text = [NSString stringWithFormat:@"%d)%@", i, [d objectForKey:@"description"]];
	cell.textLabel.text = text;
	//[text autorelease];
	return cell;
}


@end
