/**
 * @author Damien Chesneau
 */

var sendedLines;
var token;
var showHideBool = true;
var eventBus = new vertx.EventBus("/eventbus/");

var canISend = false;
var requests = [];
var nbReq = 0;

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
    canISend = true;
});

function showHide() {
    showHideBool = !showHideBool;
    if (showHideBool) {
        $('.junitTest').show();
    } else {
        $('.junitTest').hide();
    }
}

function testAll(parent){
    parent.each(function(index, element){
        sendJavaTest(element.find("code"),element.find("pre"));
    });
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

function passBloc(allRequests, current,openType,closeType){
    var nbBlocs = 1;
    while(nbBlocs!=0 && current<allRequests.length){
        current++;
        switch (allRequests[current]){
            case openType:
                nbBlocs++;
                break;
            case closeType:
                nbBlocs--;
                break;
        }
    }
    return current;
}

function addRequest(content,start, end){
    var request =  content.substring(start,end+1);
    if(request.replace(/\s/g,"").length != 0) {
        requests[nbReq++] = request;
    }
}

function splitJavaCode(allContent) {
    var start = 0;
    for (var i = 0; i < allContent.length; i++) {
        switch(allContent[i]){

            case ';':
                addRequest(allContent,start,i);
                start = i+1;
                break;

            case '{':
                i = passBloc(allContent, i,'{','}');
                addRequest(allContent,start,i);
                start = i+1;
                break;

            case '(':
                i = passBloc(allContent, i,'(',')');
                break;

        }
    }
    addRequest(allContent,start,allContent.length);
    nbReq = 0;
}

function sendAllLines(allCode){
    if(canISend) {
        canISend = false;
        var allContent = allCode.val();
        allCode.val('');
        splitJavaCode(allContent);
        sendJavaCode(requests[nbReq++]);
    }
}

function sendJavaCode(code) {
    var content = code.replace(/\r|\n/g,"");
    sendedLines = code.replace(/(\r|\n)+/g,'\n').split(/\r|\n/g);
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
    var sended = "";
    for(var i =0; i<sendedLines.length;i++){
        sended += sendedLines[i];
    }
    for ( i = 1; i < message.length; i++) {
        $("#console").append("<p>" + sended + "<br/><span  style=\"color: " + ((message[i][1] == true) ? 'green' : 'red') + "\">"+
            ((message[1][1] == true && message[i][0] != "") ? ": "+ message[i][0] : message[i][2]) + "</span></p>");
    }

    if(nbReq >= requests.length){
        canISend = true;
        nbReq = 0;
        requests = [];
    }else{
        sendJavaCode(requests[nbReq++]);
    }
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