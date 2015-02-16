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
    var data = [];

    for(var i=1;i<200;i++) {
        var obj = {
            "alias": "collection " + i,
            "count": i*2,
            "collections": "col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3col1, col2, col3"
        };

        data.push(obj);
    }

    $('#table-alias').bootstrapTable({
        data: data
    });

}