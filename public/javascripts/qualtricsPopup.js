/**
 * Pop up qualtrics survey
 */
<!--BEGIN QUALTRICS POPUP-->


$('a.modalButton').on('click', function(e) {
	alert('hello');
    var src = $(this).attr('data-src');
    var height = $(this).attr('data-height') || 300;
    var width = $(this).attr('data-width') || 400;

    $("#myModal iframe").attr({'src':src,
                        'height': height,
                        'width': width});
});

<!--END QUALTRICS POPUP-->
