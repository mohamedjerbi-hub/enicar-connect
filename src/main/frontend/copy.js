const fs = require('fs');
const https = require('https');
const path = require('path');

const srcDir = 'C:\\Users\\user\\.gemini\\antigravity\\brain\\3b4d4cb0-b076-423a-ade8-2faf9a03c49c';
const destDir = path.join(__dirname, 'src', 'assets', 'images', 'demo');

try {
    fs.copyFileSync(path.join(srcDir, 'event1_campus_1777842866126.png'), path.join(destDir, 'event1.jpg'));
    fs.copyFileSync(path.join(srcDir, 'event2_cyber_1777843122571.png'), path.join(destDir, 'event2.jpg'));
    fs.copyFileSync(path.join(srcDir, 'event3_code_1777843681730.png'), path.join(destDir, 'event3.jpg'));
    fs.copyFileSync(path.join(srcDir, 'event3_code_1777843681730.png'), path.join(destDir, 'event4.jpg'));
} catch (e) {
    console.error("Copy failed", e);
}

const file = fs.createWriteStream(path.join(destDir, 'event5.jpg'));
https.get("https://picsum.photos/id/1058/800/400", function(response) {
    if (response.statusCode === 302) {
        https.get(response.headers.location, function(res2) {
            res2.pipe(file);
        });
    } else {
        response.pipe(file);
    }
});
