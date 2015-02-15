var chart1, chart2;

var chartOptions = {
    chart: {
        renderTo: 'chart1-div',
        type: 'bar'
    },
    title: {
        text: 'Document count'
    },
    subtitle: {
        //text: 'Per Collection'
    },
    xAxis: {
        categories: ['col1','col2','col3','col1','col2','col3','col1','col2','col3','col1','col2','col3','col1','col2','col3'],
        title: {
            text: null
        }
    },
    yAxis: {
        min: 0,
        title: {
            text: '',
            align: 'high'
        },
        labels: {
            overflow: 'justify'
        }
    },
    tooltip: {
        //valueSuffix: ' millions'
    },
    plotOptions: {
        bar: {
            dataLabels: {
                enabled: true
            }
        }
    },
    legend: {
        enabled: false,
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'top',
        x: -40,
        y: 100,
        floating: true,
        borderWidth: 1,
        //backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
        shadow: true
    },
    credits: {
        enabled: false
    },
    series: [{
        name: 'Num Docs: ',
        data: [107, 31, 635, 203, 2, 107, 31, 635, 203, 2, 107, 31, 635, 203, 2]
    }]
};

function initChart() {
    var opt1 = chartOptions;
    opt1['chart']['renderTo'] = "chart1-div";
    chart1 = new Highcharts.Chart(opt1);

    var opt2 = chartOptions
    opt2['chart']['renderTo'] = "chart2-div";
    opt2['title']['text'] = 'Collection Size';
    chart2 = new Highcharts.Chart(opt2);


}

function updateChart(data) {
    chartOptions.series[0].data = data;
    chart = new Highcharts.Chart(chartOptions);
}
