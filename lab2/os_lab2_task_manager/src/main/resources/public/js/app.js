//$(function(){
//  $("#myTable").tablesorter();
//});

$(function() {

  $('.narrow-table').tablesorter({
    theme : 'blue',
    // initialize zebra striping and resizable widgets on the table
    widgets: [ "zebra", "resizable", "stickyHeaders" ],
    widgetOptions: {
      storage_useSessionStorage : true,
      resizable_addLastColumn : true
    }
  });

  $('.full-width-table').tablesorter({
    theme : 'blue',
    // initialize zebra striping and resizable widgets on the table
    widgets: [ "zebra", "resizable", "stickyHeaders" ],
    widgetOptions: {
      resizable: true,
      // These are the default column widths which are used when the table is
      // initialized or resizing is reset; note that the "Age" column is not
      // resizable, but the width can still be set to 40px here
      resizable_widths : [ '10%', '10%', '40px', '10%', '100px' ]
    }
  });

});