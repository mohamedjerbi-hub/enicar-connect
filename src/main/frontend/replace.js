const fs = require('fs');
const path = require('path');

const files = [
    'src/app/features/profile/profile.component.html',
    'src/app/features/events/events.component.html',
    'src/app/shared/navbar/navbar.component.html',
    'src/app/features/social-feed/social-feed.component.html',
    'src/app/features/groups/groups.component.html',
    'src/app/features/groups/group-detail/group-detail.component.html',
    'src/app/features/account/account.component.html',
    'src/app/features/messaging/messaging.component.html'
];

files.forEach(f => {
    let p = path.join(__dirname, f);
    if (!fs.existsSync(p)) return;
    
    let content = fs.readFileSync(p, 'utf8');

    // Replace Picsum events
    content = content.replace(/\[src\]="'https:\/\/picsum\.photos\/seed\/event' \+ \(ev\.id \|\| i\) \+ '\/800\/400'"/g, `[src]="'assets/images/demo/event' + ((ev.id || i) % 5 + 1) + '.jpg'"`);

    // Replace Picsum groups
    content = content.replace(/\[src\]="'https:\/\/picsum\.photos\/seed\/group' \+ \(g\.id \|\| i\) \+ '\/800\/400'"/g, `[src]="'assets/images/demo/group' + ((g.id || i) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/picsum\.photos\/seed\/group' \+ group\.id \+ '\/1200\/400'"/g, `[src]="'assets/images/demo/group' + ((group.id || 0) % 5 + 1) + '.jpg'"`);

    // Replace Pravatar current user
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(auth\.currentUser\(\)\?\.email \|\| 'default'\)"/g, `src="assets/images/demo/avatar1.jpg"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(user\.email \|\| 'default'\)"/g, `src="assets/images/demo/avatar1.jpg"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(editUser\.email \|\| 'default'\)"/g, `src="assets/images/demo/avatar1.jpg"`);

    // Replace Pravatar other users
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(post\.authorName \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((post.authorId || 2) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(c\.authorName \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((c.authorId || 3) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(s\.name \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((s.id || 4) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(c\.name \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((c.id || 2) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(activeConv\.name \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((activeConv.id || 2) % 5 + 1) + '.jpg'"`);
    content = content.replace(/\[src\]="'https:\/\/i\.pravatar\.cc\/150\?u=' \+ \(m\.senderName \|\| 'default'\)"/g, `[src]="'assets/images/demo/avatar' + ((m.senderId || 3) % 5 + 1) + '.jpg'"`);

    fs.writeFileSync(p, content, 'utf8');
});

console.log("Replaced successfully!");
