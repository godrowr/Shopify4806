function getCookie(cname) {
    var result = document.cookie.match(new RegExp(cname + '=([^;]+)'));
    return result && result[1];
}


async function ajax(method, url) {
    return await $.ajax({
        type: method,
        url: url,
        error: (xhr, ajaxOptions, thrownError) => alert(`Error ${xhr.status}: "${xhr.responseText}"`)
    });
}


async function checkout(cart) {
    const ccName = $("#ccName").val()
    const ccNumber = $("#ccNumber").val()

    if (ccName && ccNumber) {
        const userid = getCookie("userid");

        // make sure all items are in stock and get ids
        for (const product of cart) {
            const realProduct = await ajax("GET", product._links.self.href);
            product.inventoryNumber = realProduct.inventoryNumber;
            if (product.inventoryNumber < 1) {
                $("#cartInfo").text(`sorry, there are no more ${product.name}s left :(`)
                return;
            }
            product.id = product._links.self.href.match(/\/(\d+$)/)[1]
        }

        // empty the cart and lower the stock counts
        for (const product of cart) {
            await $.ajax({
                type: "PUT",
                url: `/products/${product.id}`,
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    name: product.name,
                    description: product.description,
                    imageUrl: product.imageUrl,
                    inventoryNumber: product.inventoryNumber - 1,
                    price: product.price
                }),
                error: (xhr, ajaxOptions, thrownError) => alert(`Error ${xhr.status}: "${xhr.responseText}"`)
            });
            deleteItem(userid, product.id)
          //  await ajax("DELETE", `/users/${userid}/shoppingCartItems/${product.id}`)
        }

        document.write("thank you for your purchase.");
        window.setTimeout(
            () => window.location = "/browseShops/",
            2000
        );
    }

    else {
        $("#errorText").text("please enter valid credit card information.");
    }
}

async function deleteItem(userid, productid){
    console.log("delete" + userid + " " + productid);

    await ajax("DELETE", `/users/${userid}/shoppingCartItems/${productid}`)
    location.reload();
}

async function loadShoppingCart(cart) {
    const userid = getCookie("userid");
    if (userid) {
        const response = await ajax("GET", `/users/${userid}/shoppingCartItems`);
        const cartItems = response._embedded.products;
        let total = 0;
        cartItems.forEach(product => {
            cart.push(product);
            total += product.price;
            let pid = product._links.self.href.match(/\/(\d+$)/)[1];
            $("#cartItems").append(`<p>${product.name} - $${product.price}</p><a onclick="deleteItem(${userid}, ${pid})">Delete</a>`);
        });
        $("#cartItems").append(`<p><b>total</b> - $${total}</p>`);
    }
}


$(document).ready(() => {
    if(!getCookie("userid")){
        $("#cartInfo").hide();
        window.location="/error-403"
    } else {
        const cart = []; // filled by loadShoppingCart and used by checkout
        loadShoppingCart(cart);
        $("#buyButton").click(() => checkout(cart));
    }
});