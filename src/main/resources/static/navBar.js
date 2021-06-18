$(document).ready(function() {
    // Check if signed in
    var signedIn;
    var username = getCookie("username");
    var userid = getCookie("userid");
    if (username && userid){
        signedIn = true;
    } else {
        signedIn = false;
    }

    // create a navbar
    var nav = document.createElement("NAV");
    nav.classList.add("navbar");
    nav.classList.add("navbar-default");
    var navContainer = document.createElement("DIV");
    navContainer.classList.add("container-fluid");
    nav.appendChild(navContainer);

    // Add header (holds logo & a dropdown button for when width is small)
    var header = document.createElement("DIV");
    header.classList.add("navbar-header");
    navContainer.appendChild(header);

    // Add dropdown button for when width is small
    var dropdownButton = document.createElement("BUTTON");
    dropdownButton.type = "button";
    dropdownButton.classList.add("navbar-toggle");
    dropdownButton.classList.add("collapsed");
    dropdownButton.setAttribute("data-toggle", "collapse");
    dropdownButton.setAttribute("data-target", "#navbarSupportedContent");
    dropdownButton.setAttribute("aria-expanded", "false");
    header.appendChild(dropdownButton);

    // add three vertical lines to dropdown button signifying dropdown list
    for (let i = 0; i < 3; i++){
        buttonIcon = document.createElement("SPAN");
        buttonIcon.classList.add("icon-bar");
        dropdownButton.appendChild(buttonIcon);
    }

    // Add a little logo to be fancy
    var aBrand = document.createElement("A");
    aBrand.classList.add("navbar-brand");
    var img = document.createElement("IMG");
    img.alt = "Shopify 2";
    img.height = 25;
    img.src = "/logo.png";
    aBrand.appendChild(img);
    header.appendChild(aBrand);

    // Create the container for all the nav buttons
    navCollapse = document.createElement("DIV");
    navCollapse.classList.add("collapse");
    navCollapse.classList.add("navbar-collapse");
    navCollapse.id="navbarSupportedContent";
    navList = document.createElement("UL");
    navList.classList.add("nav");
    navList.classList.add("navbar-nav");
    navCollapse.appendChild(navList);
    navContainer.appendChild(navCollapse);

    // Add a link for all the pages we want to be able to navigate to
    navPages = [
        {label:"Home", url:"/login/"},
        {label:"Browse Shops", url:"/browseShops/"},
    ]
    if (signedIn){
        navPages = [
           {label:"Home", url:"/login/"},
           {label:"Browse Shops", url:"/browseShops/"},
           {label:"New Shop", url: "/newshop/"},
           {label:"View Cart", url: "/shoppingcart/"}
        ]
   }
    for (obj of navPages){
        item = document.createElement("LI");

        //If the link is for the current url make this visible to the user
        if(window.location.pathname.replaceAll("/","").toLowerCase() == obj.url.replaceAll("/","").toLowerCase()){
            item.classList.add("active");
        }

        link = document.createElement("A");
        link.href = obj.url;
        link.innerHTML = obj.label;

        item.appendChild(link);
        navList.appendChild(item);
    }

    // Create auth div
    authDiv = document.createElement("DIV");
    authDiv.classList.add("navbar-right");
    navCollapse.appendChild(authDiv);

    // Create correct auth stuff
    authButton = document.createElement("BUTTON");
    authButton.classList.add("btn");
    authButton.classList.add("btn-link");
    authButton.style.padding = "15px 15px 15px 0px";
    if (signedIn){
        userInfo = document.createElement("P");
        userInfo.innerHTML = "username:" + username + " id:" + userid;
        userInfo.style.display = "inline";
        userInfo.style.padding = "15px 15px 15px 0px";
        authDiv.appendChild(userInfo);

        authButton.innerHTML = "Sign Out";
        authButton.onclick = signOut;
    } else {
        authButton.innerHTML = "Sign In";
        authButton.onclick = redirectToSignIn;

    }
    authDiv.appendChild(authButton);
    // Add this stuff to the top of the document body
    $(document.body).prepend(nav);
});

function signOut(e){
    deleteCookie("username");
    deleteCookie("userid");
    window.location="/login/";
    redirectToSignIn(e);
}

function redirectToSignIn(e){
    window.location="/login/";
}

function getCookie(cname) {
 var result = document.cookie.match(new RegExp(cname + '=([^;]+)'));
 return result && result[1];
}

function deleteCookie(cname) {
  console.log("Deleting cooking " + cname + ".");
  var d = new Date();
  d.setTime(0);
  var expires = "expires="+ d.toUTCString();
  document.cookie = cname + "=" + "shouldBeRemoved" + ";" + expires + ";path=/";
}