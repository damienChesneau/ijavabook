/**
 * Created by Damien Chesneau - contact@damienchesneau.fr on 08/11/15.
 */
var socket;
if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8989/exercice");
    socket.onmessage = function (event) {
        console.log("Data from srv: " + event.data);
        var recevedata = JSON.parse(event.data);
        switch (recevedata.t){
            case "ex":
                $("#displayExercise").html(recevedata.m);
                break;
            case "op":
                var javaCode = $("#javacode").val();
                $("#console").append("<p>"+javaCode+"</p>");
                $("#javacode").val("");
                $("#output").html(recevedata.m);
                break;
        }
    }
    socket.onopen = function (event) {
        console.log("Web Socket OK");
        send(placeValueInReq("gete","1"));

    };
    socket.onclose = function (event) {
        console.error("Web Socket closed :(");
    };
} else {
    console.log("Your browser does not support Websockets :(");
}
function send(message) {
    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        socket.send(message);
    } else {
        console.error("The socket is not opened");
    }
}
function placeValueInReq(type, value) {
    return "{\"t\": \"" + type + "\", \"m\": \"" + replaceAll(value,"\"", "\\\"") + "\"}";
}

function sendJavaCode(code) {
    send(placeValueInReq("jc", code.val()));
}

function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}
