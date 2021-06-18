$(document).ready(function() {
    if(!getCookie("userid")){
        $("body").hide()
        window.location="/error-403"
    } else {
        $('#submit_new_shop').click(createShop);
    }
});

function getCookie(cname) {
 var result = document.cookie.match(new RegExp(cname + '=([^;]+)'));
 return result && result[1];
}

function createShop() {
    const name = document.getElementById('name').value;
    const category = document.getElementById('category').value;
    const tag = document.getElementById('tag').value;

    if (name == "" || category == "" || tag == ""){
        alert("You must fill out all the info");
        return false;
    }

    const ownedBy = getCookie("userid");
    if (ownedBy){
        const newShop = {name: name,
                             category: category,
                             tag: tag,
                             ownedBy: "/users/" + ownedBy};
        $.ajax({
            type: "POST",
            url: "/shops",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(newShop),
            error: (xhr, ajaxOptions, thrownError) => alert(`Error ${xhr.status}: "${xhr.responseText}"`)
        }).then(function(data){
            const id = data._links.shop.href.split("/").pop();
            location.href = `/viewShop/?shopId=${id}`;
        });
    }
}
