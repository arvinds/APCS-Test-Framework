function sendToIndexIfLoggedIn()
{
    if(isLoggedIn())
    {
        window.location = "/index.html"
    }
}

function enforceLogin()
{
    if(isLoggedIn())
    {
        var header = document.getElementById("header");
        header.innerHTML = "Hi " + getFullName() + ", Welcome to Autograder!<br/>"
                            + "<a href='/index.html'>Announcements</a> | <a href='/submit.html'>Submit Program</a> | <a href='/submissions.html'>View Submissions</a> | <a href='/logout'>Logout</a>"
                            + "<hr>";
    }
    else
    {
        window.location = "/login.html"
    }
}

function getFirst()
{
    return getFullName().split(" ")[0];
}

function getLast()
{
    return getFullName().split(" ")[1];
}

function getFullName()
{
    if(document.cookie)
    {
        var parts = document.cookie.split(";");
        return parts[1].split("=")[1];
    }
    return false;
}

function getPassphrase()
{
    if(document.cookie)
    {
        var parts = document.cookie.split(";");
        return parts[0].split("=")[1];
    }
    return false;
}

function isLoggedIn()
{
    if(document.cookie)
    {
        return true;
    }
    else
    {
        return false;
    }
}
