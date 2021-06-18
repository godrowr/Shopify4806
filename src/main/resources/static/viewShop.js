function getCookie(cname) {
    var result = document.cookie.match(new RegExp(cname + '=([^;]+)'));
    return result && result[1];
}


function reloadProducts() {
    $("#products").empty();

    fetch("/shops/" + shopID + "/productList").then(response => response.json()).then(data => {
        console.log(data);

        for (let product of data._embedded.products) {
            console.log(product);

            const productId = product._links.self.href.match(/\/(\d+$)/)[1];

            const newProductElement = $(`<div>
                <h2>${product.name}</h2>
                <p>${product.description}</p>
                <p>Price: $${product.price}</p>
                <p>Inventory #: ${product.inventoryNumber}</p>
                <img src="${product.imageUrl}" alt="${product.name}"/>
                <button type="button" id="button${productId}">Add to cart</button>
            </div>`, {class: 'product'});

            $("#products").append(newProductElement);

            const cartbutton = $(`#button${productId}`);

            cartbutton.click(function() {
                if (product.inventoryNumber == 0){
                    alert("No more remaining!");
                } else {
                    userid = getCookie("userid");
                    $.ajax({
                        type: "POST",
                        url: `/users/${userid}/shoppingCartItems`,
                        contentType: "text/uri-list",
                        data: product._links.self.href
                    }).then(function(newProductData){
                        alert("Added to cart.")
                    });
                }

            });
            if (!signedIn) {
                $("#button").hide();
            }
        };
    });
}

let shopID = 0;
$(document).ready(() => {
    shopID = $("#shopID").text();
    reloadProducts();

    var username = getCookie("username");
    var userid = getCookie("userid");
    if (username && userid){
        signedIn = true;
    } else {
        signedIn = false;
    }

    if(!signedIn){
        $("#createProductForm").hide();
    }

    // Make sure that the shop is owned by the person logged in!
    fetch("/shops/"+shopID)
        .then(response => response.json())
        .then(data => {
            const ownerUrl = data._links.ownedBy.href;
            return fetch(ownerUrl);
        })
        .then(response => response.json())
        .then(ownerData => {
            const ownerId = ownerData._links.self.href.split("/").pop();
            if (ownerId != userid) {
                $("#createProductForm").hide();
            }
        });

    $("#submitProduct").click(function() {
        const formData = $("#createProductForm").serializeArray();
        //https://stackoverflow.com/a/11339012

        if(!formData[0].value){
            alert("Name can't be null.")
            return false;
        }
        if(!formData[4].value){
            alert("Price can't be empty.")
            return false;
        }

        const jsonData = JSON.stringify({
            name:formData[0].value,
            description:formData[1].value,
            imageUrl:formData[2].value,
            inventoryNumber: Number(formData[3].value),
            shop: "/shops/" + shopID,
            price: Number(formData[4].value)
        });

        console.log(jsonData);

        $.ajax({
            type: "POST",
            url: "/products",
            contentType: "application/json",
            dataType: "json",
            data: jsonData
        }).then(location.reload.bind(location));

        return false;
    });

})
