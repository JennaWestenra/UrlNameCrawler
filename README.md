# Url name crawler

Application crawles sites for its main page title. It has only one endpoint that accepts list of urls and returns list of responses for every url.

Run application:

`sbt run `

Application will run at host _127.0.0.1_ and port _8080_.
To get names simply make GET request for url `http://127.0.0.1:8080/urls/names` with following structure:

```
{
    "urls": [
        "https://www.instagram.com", 
        "https://www.youtube.com/", 
        "asdfgfafs.gfvcx", 
        "амсчиа"
    ]
}
```

Service will answer something like this:

```
{
    "urls": [
        {
            "url": "https://www.instagram.com",
            "name": "Instagram"
        },
        {
            "url": "https://www.youtube.com/",
            "name": "YouTube"
        },
        {
            "url": "asdfgfafs.gfvcx",
            "error": "Tcp command [Connect(asdfgfafs.gfvcx:443,None,List(),Some(10 seconds),true)] failed because of java.net.UnknownHostException: asdfgfafs.gfvcx"
        },
        {
            "url": "амсчиа",
            "error": "Could not extract main page url"
        }
    ]
}
```


After application start, you can go to `http://127.0.0.1:8080/api/v1/docs` to see swagger ui docs.