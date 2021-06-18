$(document).ready(function() {
    $('#login_page').hide();
    $('#login').click(login_user);
    $('#newuser').click(new_user);
    var username = getCookie("username");
    var userid = getCookie("userid");
    if (username && userid){
       signedIn = true;
    } else {
       signedIn = false;
    }
    if (signedIn){
        $('#login_page').hide();
        $('#home_page').show();
        load_home();
    } else {
        $('#login_page').show();
        $('#home_page').hide();
        load_users();
    }

});

function setCookie(cname, cvalue, exdays) {
  console.log("Set cookie " + cname + "=" + cvalue + ". Expires in " + exdays + " days.");
  var d = new Date();
  d.setTime(d.getTime() + (exdays*24*60*60*1000));
  var expires = "expires="+ d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function new_user(){
    console.log("creating new user");

    let data = {username: $('#username').val(),
                password: $('#password').val()};

    $('#username').empty();
    $('#password').empty();

    $.ajax({
            type: "POST",
            url: "/users",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(data)
        }).then(function(data) {
            location.reload();
        });
}

function load_users(){

    $('#username').empty();
    $('#password').empty();

    $.ajax({
        url: "/users"
    }).then(function(data) {

        $('#userlist-dummy').empty();

        if (data._embedded.users.length == 0){
            $('#userlist-dummy').append(`<td colspan="2"> No Users </td>`);
        }

         for (user_id in data._embedded.users){
              let user = data._embedded.users[user_id];
              let tr = document.createElement("tr");
              tr.innerHTML=`<td><span> ${user.username} </span></td>
                            <td><span> ${user.password} </span></td>`;
              $('#userlist-dummy').append(tr);
        }

    });
}

function login_user(){

    let login_data = {username: $('#username').val(),
                password: $('#password').val()};

    $('#username').empty();
    $('#password').empty();

    $.ajax({
        url: "/users/search/findByUsernameAndPassword",
        data: login_data,
        error: (xhr, ajaxOptions, thrownError) => alert("Failed to log in!")
    }).then(function(data) {
        var users_id =  data._links.user.href.split("/").pop();

        setCookie("username", login_data.username, 1);
        setCookie("userid", users_id, 1);
        alert('Logged in!'); found = true;
        location.reload();
    });

}

function showPassword() {
  var x = document.getElementById("curr_password");
  if (x.type === "password") {
    x.type = "text";
  } else {
    x.type = "password";
  }
}

function showUserShops() {
    var userid = getCookie("userid");

    fetch("/users/"+userid+"/shops")
        .then(response => response.json())
        .then(data => {
            let title = `<h3>Shops owned by you</h3>`;
            $('#home_page').append(title);
            for (let shop of data._embedded.shops){
                const id = shop._links.shop.href.match(/\/(\d+$)/)[1];
                let ref = `/viewShop/?shopId=${id}`;
                let shop_data = `<a href=${ref}>${shop.name}</a><br>`;
                $('#home_page').append(shop_data);
            }
        });
}

function load_home(){

    let search = {id: getCookie("userid")};

    $.ajax({
        url: "/users/search/findByid",
        data: search,
        error: (xhr, ajaxOptions, thrownError) => alert("No user exists!")
    }).then(function(data) {
        let currentUser = `<form>
        <p>Current Username: <input type="text" id="curr_username" value=${data.username}></p>
        <p>Current Password: <input type="password" id="curr_password" value=${data.password}></p><button type="button" id="button">Update</button>
        <p><input type="checkbox" onclick="showPassword()"> Show password<p></form>
        <hr>`;
        $('#home_page').append(currentUser);

        showUserShops();

        const updatebutton = $("#button");

        updatebutton.click(async function() {
            let info = {username: $('#curr_username').val(), password: $('#curr_password').val()};
            
            if (info.username == "" || info.password == ""){
                alert("You cannot have an empty username or password");
                return false;
            }

            console.log(info);
            alert("User data updated, Signing out.");
            await $.ajax({
                type: "PUT",
                url: `/users/${getCookie("userid")}`,
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(info),
                error: (xhr, ajaxOptions, thrownError) => alert(`Error ${xhr.status}: "${xhr.responseText}"`)
            });
            deleteCookie("username");
            deleteCookie("userid");
            window.location="/login/";
        });
    });


}


