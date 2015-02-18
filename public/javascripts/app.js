function getSolrCollections() {

    $.ajax({
        'type': "GET",
        'url': "/collections",
        'success':function(data) {
            alert(data)
        }
    });
}

$(document).ready(function(){

    populateAliasTable();

    populateCollectionTable();

    $('.details').click(function(){
        showCollectionDetails();
    });

    initChart();
});

function runningFormatter(value, row, index) {
    return index;
}

function showCollectionDetails() {
    $('#collection-details-modal').modal("show")
}

function populateCollectionTable() {

    var data = [];

    for(var i=1;i<200;i++) {
        var obj = {
            "collection": "collection " + i,
            "state": "active",
            "doc-count": i*100,
            "size": i*3 + "MB",
            "details":"<span class='details'>Details</span>"
        }

        data.push(obj);
    }

    $('#table').bootstrapTable({
        data: data
    });

}

function populateAliasTable() {


    $.ajax({
        'type': "GET",
        'url': "/api/solr/alias",
        'success':function(response) {
            console.log(response);
            var data = [];

            response = {}
            $.each(response, function(aliasName, collectionList) {
                console.log(aliasName);
                var cols = collectionList.split(",");

                data.push({
                    "alias": aliasName,
                    "count": cols.length,
                    "collections": cols.join(", ")
                });
            });

            $('#table-alias').bootstrapTable({
                data: data
            });
        }
    });



}

