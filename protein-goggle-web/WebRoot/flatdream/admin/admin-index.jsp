
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">
  <link rel="shortcut icon" href="images/icon.png">

  <title>Flat Dream</title>

  <!-- Bootstrap core CSS -->
  <link href="js/bootstrap/dist/css/bootstrap.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="js/jquery.gritter/css/jquery.gritter.css" />
  <link rel="stylesheet" href="fonts/font-awesome-4/css/font-awesome.min.css">

  <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
    <script src="../../assets/js/html5shiv.js"></script>
    <script src="../../assets/js/respond.min.js"></script>
  <![endif]-->
  <link rel="stylesheet" type="text/css" href="js/jquery.nanoscroller/nanoscroller.css" />

  <link type="text/css" rel="stylesheet" href="js/prettify/prettify.css" />
  
  <link href="css/style.css" rel="stylesheet" />  
    
</head>
<body class="animated">

<div id="cl-wrapper">

  <div class="cl-sidebar">
    <div class="cl-toggle"><i class="fa fa-bars"></i></div>
    <div class="cl-navblock">
      <div class="menu-space">
        <div class="content">
          <div class="sidebar-logo">
            <div class="logo">
                <a href="index2.html"></a>
            </div>
          </div>
         
         <ul class="cl-vnavigation">
            <li class="active"><a href="welcome.html"><i class="fa fa-home"></i><span>Welcome</span></a>
        
            </li>
            <li><a href="the_team.html"><i class="fa fa-desktop"></i><span>The Team</span></a>
             
            </li>
            <li><a href="collaborators.html"><i class="fa fa-smile-o"></i><span>Collaborators</span></a>
              
            </li>
            <li><a href="example_data.html"><i class="fa fa-list-alt"></i><span>Example Data</span></a>
              
            </li>
            <li><a href="submit_a_job.html"><i class="fa fa-table"></i><span>Submit a Job</span></a>
            
            </li>
            <li><a href="my_jobs.html"><i class="fa fa-map-marker nav-icon"></i><span>My Jobs</span></a>
              
            </li>
            <li><a href="references.html"><i class="fa fa-envelope nav-icon"></i><span>References</span></a>
              
            </li>
            <li  ><a href="forum.html"><i class="fa fa-text-height"></i><span>Forum</span></a></li>
            <li  ><a href="employment.html"><i class="fa fa-bar-chart-o"></i><span>Employment</span></a></li>
            <li><a href="contact_us.html"><i class="fa fa-file"></i><span>Contact Us</span></a></li>

          </ul>
        </div>
      </div>
      <div class="text-right collapse-button" style="padding:7px 9px;">
        <input type="text" class="form-control search" placeholder="Search..." />
        <button id="sidebar-collapse" class="btn btn-default" style=""><i style="color:#fff;" class="fa fa-angle-left"></i></button>
      </div>
    </div>
  </div>
  <div class="container-fluid" id="pcont">
   <!-- TOP NAVBAR -->
  <div id="head-nav" class="navbar navbar-default">
    <div class="container-fluid">
      <div class="navbar-collapse">
    <div class="content">
    


    <div class="col-sm-offset-10">
    <button id="logout-btn" class="btn btn-primary" type="submit">&nbsp;&nbsp;Logout &nbsp;&nbsp;&nbsp;</button>
    
    
  
    </div>
    
    
    
    </div>
      </div><!--/.nav-collapse animate-collapse -->
    </div>
  </div>
  
    
  <div class="cl-mcont">    <div class="page-head">
      <ol class="breadcrumb">

        <li class="active">Admin</li>
      </ol>
    </div>  
      <div class="row">
        
        
        <div class="col-sm-12 col-md-12">

          <div class="block-flat">
            <div class="header">              
              <h1>Welcome</h1>
            </div>
            <div class="content">
              
              <p>this is the admin page</p>
              <div id="jobQueue"></div>
            </div>
          </div>

          
        </div>      
      </div>
      <div class="row">
        
      </div>
      
        </div>
  
  </div> 
  
</div>
<!-- Right Chat-->
<nav class="cbp-spmenu cbp-spmenu-vertical cbp-spmenu-right side-chat">
  <div class="header">
    <h3>Chat</h3>
  </div>
  <div class="sub-header">
    <div class="icon"><i class="fa fa-user"></i></div> <p>Online (4)</p>
  </div>
  <div class="content">
    <p class="title">Family</p>
    <ul class="nav nav-pills nav-stacked contacts">
      <li class="online"><a href="#"><i class="fa fa-circle-o"></i> Michael Smith</a></li>
      <li class="online"><a href="#"><i class="fa fa-circle-o"></i> John Doe</a></li>
      <li class="online"><a href="#"><i class="fa fa-circle-o"></i> Richard Avedon</a></li>
      <li class="busy"><a href="#"><i class="fa fa-circle-o"></i> Allen Collins</a></li>
    </ul>
    
    <p class="title">Friends</p>
    <ul class="nav nav-pills nav-stacked contacts">
      <li class="online"><a href="#"><i class="fa fa-circle-o"></i> Jaime Garzon</a></li>
      <li class="outside"><a href="#"><i class="fa fa-circle-o"></i> Dave Grohl</a></li>
      <li><a href="#"><i class="fa fa-circle-o"></i> Victor Jara</a></li>
    </ul>   
    
    <p class="title">Work</p>
    <ul class="nav nav-pills nav-stacked contacts">
      <li><a href="#"><i class="fa fa-circle-o"></i> Ansel Adams</a></li>
      <li><a href="#"><i class="fa fa-circle-o"></i> Gustavo Cerati</a></li>
    </ul>
    
  </div>
  <div id="chat-box">
    <div class="header">
      <span>Richard Avedon</span>
      <a class="close"><i class="fa fa-times"></i></a>
    </div>
    <div class="messages nano nscroller">
      <div class="content">
        <ul class="conversation">
          <li class="odd">
            <p>Hi Jane, how are you?</p>
          </li>
          <li class="text-right">
            <p>Hello I was looking for you</p>
          </li>
          <li class="odd">
            <p>Tell me what you need?</p>
          </li>
          <li class="text-right">
            <p>Sorry, I'm late... see you</p>
          </li>
          <li class="odd unread">
            <p>OK, call me later...</p>
          </li>
        </ul>
      </div>
    </div>
    <div class="chat-input">
      <div class="input-group">
        <input type="text" class="form-control" placeholder="Enter a message...">
        <span class="input-group-btn">
        <button type="button" class="btn btn-primary">Send</button>
        </span>
      </div>
    </div>
  </div>
</nav>
  
  <script src="js/jquery.js"></script>
  <script src="js/jquery.cookie/jquery.cookie.js"></script>
  <script src="js/jquery.pushmenu/js/jPushMenu.js"></script>
  <script type="text/javascript" src="js/jquery.nanoscroller/jquery.nanoscroller.js"></script>
  <script type="text/javascript" src="js/jquery.sparkline/jquery.sparkline.min.js"></script>
  <script type="text/javascript" src="js/jquery.ui/jquery-ui.js" ></script>
  <script type="text/javascript" src="js/jquery.gritter/js/jquery.gritter.js"></script>
  <script type="text/javascript" src="js/behaviour/core.js"></script>

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
  <script src="js/bootstrap/dist/js/bootstrap.min.js"></script>
      <style type="text/css">
    #color-switcher{
      position:fixed;
      background:#272930;
      padding:10px;
      top:50%;
      right:0;
      margin-right:-109px;
    }
    
    #color-switcher .toggle{
      cursor:pointer;
      font-size:15px;
      color: #FFF;
      display:block;
      position:absolute;
      padding:4px 10px;
      background:#272930;
      width:25px;
      height:30px;
      left:-24px;
      top:22px;
    }
    
    #color-switcher p{color: rgba(255, 255, 255, 0.6);font-size:12px;margin-bottom:3px;}
    #color-switcher .palette{padding:1px;}
    #color-switcher .color{width:15px;height:15px;display:inline-block;cursor:pointer;}
    #color-switcher .color.purple{background:#7761A7;}
    #color-switcher .color.green{background:#19B698;}
    #color-switcher .color.red{background:#EA6153;}
    #color-switcher .color.blue{background:#54ADE9;}
    #color-switcher .color.orange{background:#FB7849;}
    #color-switcher .color.prusia{background:#476077;}
    #color-switcher .color.yellow{background:#fec35d;}
    #color-switcher .color.pink{background:#ea6c9c;}
    #color-switcher .color.brown{background:#9d6835;}
    #color-switcher .color.gray{background:#afb5b8;}
 </style>
  <div id="color-switcher">
    <p>Select Color</p>
    <div class="palette">
      <div class="color purple" data-color="purple"></div>
      <div class="color green" data-color="green"></div>
      <div class="color red" data-color="red"></div>
      <div class="color blue" data-color="blue"></div>
      <div class="color orange" data-color="orange"></div>
    </div>
    <div class="palette">
      <div class="color prusia" data-color="prusia"></div>
      <div class="color yellow" data-color="yellow"></div>
      <div class="color pink" data-color="pink"></div>
      <div class="color brown" data-color="brown"></div>
      <div class="color gray" data-color="gray"></div>
    </div>
    <div class="toggle"><i class="fa fa-angle-left"></i></div>
  </div>

  <script type="text/javascript">
    var link = $('link[href="css/style.css"]');
    
    if($.cookie("css")) {
      link.attr("href",'css/skin-' + $.cookie("css") + '.css');
    }
    
    $.ajax({
          url: '/flatdream/admin/getQueue', 
          cache: false,
          
          dataType: "html",

          success: function(result){
            $('jobQueue').append(result);
          } 
        });


    $(function(){
      $("#color-switcher .toggle").click(function(){
        var s = $(this).parent();
        if(s.hasClass("open")){
          s.animate({'margin-right':'-109px'},400).toggleClass("open");
        }else{
          s.animate({'margin-right':'0'},400).toggleClass("open");
        }
      });
      
      $("#color-switcher .color").click(function(){
        var color = $(this).data("color");
        $("body").fadeOut(function(){
          //link.attr('href','css/skin-' + color + '.css');
          $.cookie("css", color, {expires: 365, path: '/'});
          window.location.href = "";
          $(this).fadeIn("slow");
        });
      });

      $("#logout-btn").click(function(){
        $.ajax({
          url: '/flatdream/admin/logout', 
          cache: false,
          
          dataType: "html",

          success: function(result){
            if(result == 'success'){
              window.location.href = "admin";
            }else{
              alert(result);
            }
          } 
        });
      });


    });
  </script> <script type="text/javascript" src="js/prettify/run_prettify.js"></script>
  
  
<script type="text/javascript" src="js/jquery.flot/jquery.flot.js"></script>
<script type="text/javascript" src="js/jquery.flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="js/jquery.flot/jquery.flot.resize.js"></script>
<script type="text/javascript" src="js/jquery.flot/jquery.flot.labels.js"></script>
</body>
</html>
