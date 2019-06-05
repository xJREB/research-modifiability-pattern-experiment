gnome-terminal --title=$(basename "$0") -e 'bash -c "

npm install && npm start

exec bash"'
