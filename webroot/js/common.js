/**
 * @author Damien Chesneau
 */

if (window.location.hostname != "localhost") {
    document.location.href = "./wrongacces.html";
}

function showError(message) {
    $("#error").html('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a><strong>Error ! </strong>' + message + '.</div>');
    console.log("error");

}
