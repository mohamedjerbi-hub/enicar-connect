const https = require('https');
const fs = require('fs');
const path = require('path');

const dir = path.join(__dirname, 'src', 'assets', 'images', 'demo');

function download(url, dest) {
    return new Promise((resolve, reject) => {
        const file = fs.createWriteStream(dest);
        https.get(url, function(response) {
            if (response.statusCode === 302) {
                download(response.headers.location, dest).then(resolve).catch(reject);
                return;
            }
            response.pipe(file);
            file.on('finish', function() {
                file.close(resolve);
            });
        }).on('error', function(err) {
            fs.unlink(dest, () => {});
            reject(err);
        });
    });
}

async function run() {
    console.log("Downloading avatars...");
    await download('https://i.pravatar.cc/150?img=11', path.join(dir, 'avatar1.jpg')); // Man (Mohamed)
    await download('https://i.pravatar.cc/150?img=12', path.join(dir, 'avatar2.jpg')); // Man (Sami)
    await download('https://i.pravatar.cc/150?img=5', path.join(dir, 'avatar3.jpg'));
    await download('https://i.pravatar.cc/150?img=8', path.join(dir, 'avatar4.jpg'));
    await download('https://i.pravatar.cc/150?img=9', path.join(dir, 'avatar5.jpg'));

    console.log("Downloading events...");
    await download('https://picsum.photos/id/1018/800/400', path.join(dir, 'event1.jpg'));
    await download('https://picsum.photos/id/1015/800/400', path.join(dir, 'event2.jpg'));
    await download('https://picsum.photos/id/1016/800/400', path.join(dir, 'event3.jpg'));
    await download('https://picsum.photos/id/1019/800/400', path.join(dir, 'event4.jpg'));
    await download('https://picsum.photos/id/1020/800/400', path.join(dir, 'event5.jpg'));

    console.log("Done");
}

run();
