/***************************************/
/* Custom Javascript for James website */
/***************************************/

/* 
Examples: append xs screen version of labels
          right after the example headings
*/
$('#examples').ready(function(){
    $('#examples .example-header').each(function(){
        // create list for xs labels
        list = $('<ul>', {
                    class: 'list-group visible-xs'
                });
        // convert labels, add to list (backwards)
        $($(this).children('span.hidden-xs').children().get().reverse()).each(function(){
            // get right label
            if($(this).hasClass('label-info')){ type = 'list-group-item-info' };
            if($(this).hasClass('label-warning')){ type = 'list-group-item-warning' };
            if($(this).hasClass('label-success')){ type = 'list-group-item-success' };
            if($(this).hasClass('label-danger')){ type = 'list-group-item-danger' };
            // append to list
            $('<li>', {
                class: 'list-group-item ' + type,
                html: $(this).html()
            }).appendTo($(list));
        });
        // append xs label list right after the example header
        $(this).after(list);
    });
});