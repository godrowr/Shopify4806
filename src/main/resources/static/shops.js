
async function makeShopBlock(shopJson) {
    const div = document.createElement('div');
    div.setAttribute('class', 'card-body')
    const name = document.createElement('h2');
    name.setAttribute('class', 'card-title');
    const owner = document.createElement('p');
    const category = document.createElement('p');
    const tag = document.createElement('p');
    const link = document.createElement('a');
    link.setAttribute('class', 'btn btn-primary')

    name.innerText = shopJson.name;


    const ownerNamePromise = $.ajax({
        url: shopJson._links.ownedBy.href
    }).then(function(user){ //gets user from reference
        owner.innerText = user.username;
    });

    category.innerText = shopJson.category;
    tag.innerText = shopJson.tag;

    link.innerText = "Visit shop";
    const id = shopJson._links.shop.href.match(/\/(\d+$)/)[1];
    link.href = `/viewShop/?shopId=${id}`;

    div.appendChild(name);
    div.appendChild(owner);
    div.appendChild(category);
    div.appendChild(tag);
    div.appendChild(link);

    await ownerNamePromise;

    return div;
}

function addShopsToPage(shops) {
    const shopsDiv = document.getElementById('shopsList');

    shopsDiv.innerHtml = "";
    shopsDiv.innerText = "";
    console.log("shops", shops);
    for (let shop of shops) {
        makeShopBlock(shop).then(newDiv => {
            shopsDiv.appendChild(newDiv);
        });
    }
}

async function searchShops(event) {
    event.preventDefault();
    const searchQuery = document.getElementById('shopName').value;
    const urls = [
        `/shops/search/findByNameIgnoreCase/?name=${searchQuery}`,
        `/shops/search/findByOwnedByUsernameIgnoreCase/?username=${searchQuery}`,
        `/shops/search/findByCategoryIgnoreCase/?category=${searchQuery}`,
        `/shops/search/findByTagIgnoreCase/?tag=${searchQuery}`,
        `/shops/search/findShopsStartingWith/?name=${searchQuery}`,
    ];

    // Fetch all of the urls
    const searchResults = await Promise.all(urls.map(url => fetch(url).then(response => response.json())));

    // De-duplicate the resulting shop objects
    const deDuplicated = new Map();

    for (let result of searchResults) {
        result = result._embedded.shops;

        for (let shop of result) {
            deDuplicated.set(shop._links.shop.href, shop);
        }
    }

    console.log(deDuplicated);

    const shopsDiv = document.getElementById('shopsList');

    if (deDuplicated.size == 0) {
        shopsDiv.innerText = "No shops found!";
    } else {
        addShopsToPage(Array.from(deDuplicated, pair => pair[1]));
    }
    return false;
}

$(document).ready(() => {
    $("#searchForm").on("submit", searchShops);

    fetch("/shops")
        .then(response => response.json())
        .then(data => {
            addShopsToPage(data._embedded.shops);
        });
});