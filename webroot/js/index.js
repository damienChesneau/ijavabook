/**
 * Created by Damien Chesneau - contact@damienchesneau.fr on 08/11/15.
 */
var socket;
if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8989/exercice");
    socket.onmessage = function (event) {
        console.log("Data from srv: " + event.data);
    }
    socket.onopen = function (event) {
        console.log("Web Socket OK");
    };
    socket.onclose = function (event) {
        alert("Web Socket closed :(");
    };
} else {
    alert("Your browser does not support Websockets :(");
}
function send(message) {
    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        socket.send(message);
    } else {
        alert("The socket is not opened");
    }
}
