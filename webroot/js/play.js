/**
 * @author Damien Chesneau
 */

var sendedLines;
var token;
var showHideBool = true;
var eventBus = new vertx.EventBus("/eventbus/");
eventBus.onopen = function () {
    eventBus.registerHandler(getParameterByName("value"), function (message) {
        actionOnResponse(JSON.parse(message));
    });
};

$.ajax({
    dataType: "json",
    method: "POST",
    url: "/exercice",
    data: getParameterByName("value")
}).done(function (msg) {
    if (msg.t !== "tex") {
        console.error("Wrong message for a new exercise.");
        return;
    }
    displayExercise(msg.m);
});

function showHide() {
    showHideBool = !showHideBool;
    if (showHideBool) {
        $('.junitTest').show();
    } else {
        $('.junitTest').hide();
    }
}

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
        case "er":
            showError(jsonMessage.m);
            break;
        case "op":
            manageSingleLineConsole(jsonMessage.m);
            break;
        default:
            console.error("Not supported " + jsonMessage.t + " operation.")
    }
}

function send(url,message,callback) {
    $.ajax({
        dataType: "json",
        method: "POST",
        url: url,
        data: JSON.stringify(message)
    }).done(function (msg) {
        callback(msg);
    });
}
function sendOnClose(message) {
    $.ajax({
        dataType: "json",
        method: "POST",
        url: "/closeexercice",
        data: JSON.stringify(message)
    }).done(function (msg) {
        manageSingleLineConsole(msg.m);
    });
}

function placeValueInReq(type, value) {
    return {"t": type, "m": value};
}
//noinspection JSUnusedGlobalSymbols
/**
 * Used for junit test so is ok if is not used.
 * @param code
 * @param result
 */
function sendJavaTest(code,result){
    var content = code.text().replace(/\r/g, "\\n");
    content = content.replace("\n", "");
    var jsonArray = new Array();
    jsonArray.push(placeValueInReq("to", token));
    jsonArray.push(placeValueInReq("rjt", '{'+content+'}'));
    placeValueInReq("rjt", jsonArray);
    send("/junittest",jsonArray,function(m){
        if(m == 'FAIL'){
            result.css('color','red');
        }else{
            result.css('color','green');
        }
    });
}

function sendJavaCode(code) {
    //var content = code.val().replace(/\r\n|\r|\n/g, "\\n");
    var content = code.val().replace(/\r/g, "\\n");
    content = content.replace("\n", "");
    sendedLines = code.val().split("\n");
    var jsonArray = [];
    jsonArray.push(placeValueInReq("to", token));
    jsonArray.push(placeValueInReq("jc", (content)));
    placeValueInReq("jc", jsonArray);
    send("/javacode",jsonArray,actionOnResponse);
}

//function verifyCode(line) {
//
//}

//function replaceAll(str, find, replace) {
//    return str.replace(new RegExp(find, 'g'), replace);
//}
function manageSingleLineConsole(message) {
    $("#output").html(message[0][0]);
    var sended = sendedLines[0];
    for(var i =1; i<sendedLines.length;i++){
        sended += '<br/>'+sendedLines[i];
    }
    for ( i = 1; i < message.length; i++) {
        $("#console").append("<p>" + sended + "<br/><span  style=\"color: " + ((message[i][1] == true) ? 'green' : 'red') + "\">"+
            ((message[1][1] == true && message[i][0] != "") ? ": "+ message[i][0] : message[i][2]) + "</span></p>");
    }
    $("#javacode").val("");
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

$(window).bind('beforeunload', function () {
    sendOnClose(placeValueInReq("cex", new Array(""+token, getParameterByName("value"))));
});