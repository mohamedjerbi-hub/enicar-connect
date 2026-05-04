const https = require('https');
const fs = require('fs');
const path = require('path');

const dir = path.join(__dirname, 'src', 'assets', 'images', 'demo');
if (!fs.existsSync(dir)){
    fs.mkdirSync(dir, { recursive: true });
}

function download(url, dest) {
    return new Promise((resolve, reject) => {
        const file = fs.createWriteStream(dest);
        https.get(url, function(response) {
            if (response.statusCode === 302) {
                // handle redirect
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
    for (let i = 1; i <= 5; i++) {
        await download(`https://i.pravatar.cc/150?img=${i}`, path.join(dir, `avatar${i}.jpg`));
        await download(`https://picsum.photos/seed/event${i}/800/400`, path.join(dir, `event${i}.jpg`));
        await download(`https://picsum.photos/seed/group${i}/800/400`, path.join(dir, `group${i}.jpg`));
    }
    console.log("Done downloading");
}

run();
