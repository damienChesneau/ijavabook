/**
 * @author Damien Chesneau
 */
//var socket;

var sendedLines;
var token;
var eventBus = new vertx.EventBus("/eventbus/");
eventBus.onopen = function () {
    console.log("Event bus connected !");
    eventBus.registerHandler(getParameterByName("value"), function (message) {
        actionOnResponse(JSON.parse(message));
    });
};


$.ajax({
    dataType: "json",
    method: "POST",
    url: "/exercice",
    data: getParameterByName("value"),
}).done(function (msg) {
    if (msg.t !== "tex") {
        console.error("Wrong message for a new exercise.");
        return;
    }
    displayExercise(msg.m);
});

function displayExercise(message) {
    for (var i = 0; i < message.length; i++) {
        actionOnResponse(JSON.parse(message[i]));
    }
}
function actionOnResponse(jsonMessage) {
    switch (jsonMessage.t) {
        case "ex":
            $("#displayExercise").html(jsonMessage.m);
            break;
        case "nt":
            token = parseInt(jsonMessage.m);
            break;
        default:
            console.error("Not supported " + jsonMessage.t + " operation.")
    }
}

function send(message) {
    $.ajax({
        dataType: "json",
        method: "POST",
        url: "/javacode",
        data: JSON.stringify(message)
    }).done(function (msg) {
        manageSingleLineConsole(msg.m);
    });
}

function placeValueInReq(type, value) {
    return {"t": type, "m": value};
}

function sendJavaCode(code) {
    var content = code.val().replace(/\r\n|\r|\n/g, "\\n");
    sendedLines = code.val().split("\n");
    var jsonArray = new Array();
    jsonArray.push(placeValueInReq("to", token));
    jsonArray.push(placeValueInReq("jc", (content)));
    placeValueInReq("tjc", jsonArray);
    send(jsonArray);
}

function verifyCode(line) {

}

function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}
function manageSingleLineConsole(message) {
    $("#output").html(message[0][0]);
    for (var i = 1; i < message.length; i++) {
        $("#console").append("<p style=\"color: " + ((message[i][1] == true) ? 'green' : 'red') + "\" >" + sendedLines[i - 1] +
            ((message[1][1] == true && message[i][0] != "") ? " Expression value =" + message[i][0] : "") + "</p>");
    }
    $("#javacode").val("");
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    console.log(results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " ")));
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
