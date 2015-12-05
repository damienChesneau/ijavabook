/**
 * Created by Damien Chesneau - contact@damienchesneau.fr on 08/11/15.
 */
var socket;
var sendedLines;
if (window.WebSocket) {
    socket = new WebSocket("ws://localhost:8989/exercice");
    socket.onmessage = function (event) {
        var recevedata = JSON.parse(event.data);
        switch (recevedata.t) {
            case "ex":
                $("#displayExercise").html(recevedata.m);
                break;
            case "op":
                var javaCode = $("#javacode").val();
                var message = (recevedata.m);
                manageSingleLineConsole(message);
                $("#output").html(message[0][0]);
                break;
        }
    }
    socket.onopen = function (event) {
        var value = getParameterByName("value");
        send(placeValueInReq("gete", value));
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
    return "{\"t\": \"" + type + "\", \"m\": \"" + replaceAll(value, "\"", "\\\"") + "\"}";
}

function sendJavaCode(code) {
    var content = code.val().replace(/\r\n|\r|\n/g, "\\n");
    sendedLines = code.val().split("\n");
    send(placeValueInReq("jc", content));
}
function verifyCode(line) {

}

function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}
function manageSingleLineConsole(message) {
    for (i = 1; i < message.length; i++) {
        $("#console").append("<p style=\"color: " + ((message[i][1] == true) ? 'green' : 'red') + "\" >" + sendedLines[i - 1] +
            ((message[1][1] == true && message[i][0] != "") ? " Expression value =" + message[i][0] : "") + "</p>");
    }
    $("#javacode").val("");
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
