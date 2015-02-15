function getSolrCollections() {

    $.ajax({
        'type': "GET",
        'url': "/collections",
        'success':function(data) {
            alert(data)
        }
    });


}

$(document).ready(function() {
    $('#collection-stats-table').find('table').first().DataTable();
})
