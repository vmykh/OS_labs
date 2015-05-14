$(function() {


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

  // $(".process-line td input[id]").click(function(event) {
  //   var pid = $(event.target).attr('id');
  //   alert(pid);
  // });



});

window.onload = function(){
  var UPDATE_PERIOD = 1500;

  var update_processes_info = function(){
    $.getJSON('/currentProcessesInfo', function(jdata) {
      var td_template = "<td>value</td>"

      var html = ""
      for (var i = 0; i < jdata.length; i++) {
        var jd = jdata[i];

        var process_line = "<tr class=\"process-line\">"
                + td_template.replace("value", jd.pid)
                + td_template.replace("value", jd.name)
                + td_template.replace("value", jd.state)
                + td_template.replace("value", jd.username)
                + td_template.replace("value", jd.cpu)
                + td_template.replace("value", jd.memory)
                + td_template.replace("value", "<input class=\"kill-button\" value=\"kill\" type=\"button\" id=\"process" + jd.pid + "\">")
                + "</tr>";

        html += process_line;       
      };

      $(".full-width-table tbody").empty(); 
            $(".full-width-table tbody").append(html);

            // let the plugin know that we made a update 
            $(".full-width-table").trigger("update");
      });

    $(".kill-button").click(function(event){
      // alert("before sending post request");
      var src_id = event.target.id;
      var pid = src_id.replace("process", "");

      var url = "/killProcess/" + pid;
      $.post( url, function( data ) {
        
      });
      // alert("post request sent. url: " + url);
    });
    setTimeout(update_processes_info, UPDATE_PERIOD);
  }



  setTimeout(update_processes_info, UPDATE_PERIOD);
}