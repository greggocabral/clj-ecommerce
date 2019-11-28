const stripe = Stripe('pk_test_8P3HEEsseBhnXwN0vIlQf15B00IwAbLp8O');

const elements = stripe.elements();
const cardElement = elements.create('card');
cardElement.mount('#card-element');

cardButton.addEventListener('click', function(ev) {
  ev.preventDefault();
  stripe.createPaymentMethod('card', cardElement, {
    billing_details: {name: cardholderName.value}
  }).then(handlePaymentMethodResult);
});

function handlePaymentMethodResult(result) {
  if (result.error) {
    // log error
    console.log(result.error);
  } else {
    // submit form with pmId
    let form = document.getElementById('paymentForm');
    let input = document.createElement("input");
    input.name = "pmId";
    input.type = "hidden";
    input.value = result.paymentMethod.id;
    form.appendChild(input);
    form.submit();
  }
}
