var _ = require("lodash-node");

module.exports = function(client) {
  client.connect(function(err) {
    if(err) {
      return console.error('Could not connect to postgres', err);
    }
    var query = client.query(
      "SELECT view_id, config" +
      " FROM portti_view_bundle_seq" +
      " WHERE" +
      " bundle_id = (SELECT id FROM portti_bundle WHERE name='mapfull') ORDER BY view_id"
    );

    var rowCount = 0;
    var updateCount = 0;
    var finished = false;
    query.on("row", function(row) {
      rowCount++;

      var config = {};
      try {
          config = JSON.parse(row.config);
      }
      catch(e) {
          console.error("Unable to parse config for view " + row.view_id + ". Error:'", e, "'. Please update manually! Config:\r\n",row.config);
          updateCount++;
          return;
      }

      if (config && config.mapOptions && config.mapOptions.resolutions) {
        config.mapOptions.resolutions.push(0.25);
      }

      var updatedConfig = JSON.stringify(config);

      var updateQuery = ("UPDATE portti_view_bundle_seq SET config='" + updatedConfig + "' WHERE" +
              " bundle_id = (SELECT id FROM portti_bundle WHERE name = 'mapfull') AND view_id=" + row.view_id);

      client.query(updateQuery, function(err, res) {
        if(err) throw err;

        updateCount++;
        if((updateCount === rowCount) && finished) {
          console.log(updateCount + ' of ' + rowCount + ' rows updated');
          client.end();
        }
      });
    });

    query.on("end", function(row) {
      finished = true;
    });
  });

}

