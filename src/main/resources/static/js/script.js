
const toggleSidebar=()=>{
  if ($(".sidebar").is(":visible")){

      $(".sidebar").css("display","none");
      $(".content").css("margin-left","0%");


    } else{
      $(".sidebar").css("display","block");
      $(".content").css("margin-left","20%");

    }



};


const  Search=()=>{
   let query=$("#search-input").val();
    console.log(query);
    if(query==''){
        $(".search-result").hide();


    }else{
        console.log(query)

        // req sending to server
        let url=`http://localhost:8080/search/${query}`;
        fetch(url).then(response=>{
           return response.json();
        }).then((data)=>{
           // data coming
            let text=`<div class="list-group">`
            data.forEach((contact)=>{
                text+=`<a href='/user/contact/${contact.cId}' class="list-group-item list-group-item-action">${contact.name}</a>`
            });

            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });





        $(".search-result").show();
    }


};
/// payment handling code starts here


// req to server
const paymentStart=()=>{
    console.log("hello")
    let amount=$("#payment_field").val();
    console.log(amount);
    if (amount=='' || amount==null){
        // alert("amount is required!!");
        swal(" Failed !!","Amount is Required !!","error");

        return;
    }

    // using ajax to send req to server jquery ajax
$.ajax({
        url: '/user/create_order',
    data:JSON.stringify({amount:amount,info:'order_request'}),
    contentType:'application/json',
    type:'POST',
    dataType:'json',
    success:function (response){
            if (response.status==="created"){
                // opening payment form
               let options={
                   key:'rzp_test_YZDWmInzn4UUCx',
                   amount:response.amount,
                   currency:'INR',
                   name:'Smart contact Manager',
                   description:'donation',
                   order_id:response.id,
                   success:function (response){
                       console.log(response.razorpay_payment_id);
                       console.log(response.razorpay_order_id);
                       console.log(response.razorpay_signature);
                       console.log("payment success")
                       // alert("congrats !! payment successful")





                       swal("Good job!","Congrats !! Payment successful !!","success");
                       updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,'paid');

                   },
                   prefill: {
                      name: "",
                       email:"",
                     contact: "",
                   },
                   notes: {
                       "address": "Coding Done by tridev"
                   },
                   theme: {
                       "color": "#3399cc",
                   },
               };
                let rzp1 = new Razorpay(options);
                rzp1.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    // alert("payment failed")
                    swal(" Failed !!","Oops Payment failed !!","error");

                });
                rzp1.open();

            }
        console.log(response);
    },
    error:function (error){
        console.log(error);
    },

    });


};


function updatePaymentOnServer (payment_id, order_id, status)  {
    $.ajax({
        url: '/user/update_order',
        data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {

                swal("Good job!", "Congrats !! Payment successful !!", "success");

        },
        error: function (error) {
            swal("Failed !!", "Your payment is Successful, but we did not get it on server. We will get back to you soon!", "error");
        },
    });
}












