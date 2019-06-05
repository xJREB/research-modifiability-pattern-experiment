gnome-terminal --title=$(basename "$0") -e 'bash -c "

cd ../WebUI
npm install && npm start

exec bash"'
