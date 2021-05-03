$(document).ready(function () {
    $("#btnCancel").on("click", function () {
        window.location.href = moduleURL;
    });

    $("#fileImageInput").on("change", function () {
        let fileSize = this.files[0].size;

        if (fileSize > (1024 * 1024)) {
            this.setCustomValidity("You must choose an image less than 1MB");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    })
});

function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();

    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }

    reader.readAsDataURL(file);
}