function getSolrCollections() {

    $.ajax({
        'type': "GET",
        'url': "/collections",
        'success':function(data) {
            alert(data)
        }
    });

}