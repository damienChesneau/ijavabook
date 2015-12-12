/**
 * @author Damien Chesneau
 */
$.ajax({
    method: "GET",
    url: "/getallexercices",
}).done(function (msg) {
    diplayExercises(msg.m);
});

function diplayExercises(allExoIntab) {
    var va = Math.floor((Math.random() * 10) + 1) % allExoIntab.length;
    console.log(va);
    $("#randomex").attr("href", "play.html?value=" + allExoIntab[va]);
    var secondtab = allExoIntab.splice(0, allExoIntab.length / 2);
    var thirdtab = allExoIntab.splice((allExoIntab.length / 2) - 1, allExoIntab.length);
    for (i = 0; i < secondtab.length; i++) {
        appendMessage("listeex1", secondtab[i]);
    }
    for (i = 0; i < thirdtab.length; i++) {
        appendMessage("listeex2", thirdtab[i]);
    }
}
function appendMessage(divid, filename) {
    $("#" + divid).append(" \<h4>" + filename + "\</h4><p><a href=\"play.html?value=" + filename + "\"\>Do this exercise</a></p>");
}