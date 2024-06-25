using UnityEngine;
using UnityEngine.UI;

public class ShowSkuPriceLabel : MonoBehaviour
{
    public Button ospButton;
    [HideInInspector]
    public Text buttonText;

    void Start()
    {
        buttonText = ospButton.GetComponentInChildren<Text>();
        UpdateButtonText();
    }

    void UpdateButtonText()
    {
        OSPButton[] buttons = FindObjectsOfType<OSPButton>();
        foreach (OSPButton button in buttons)
        {
            if (button.GetProduct() == "prod1"){
                buttonText.text = button.GetSkuLocalPriceLabel();
            } 
        }
        //For more info check https://docs.catappult.io/docs/osp#3-create-a-web-service-endpoint-to-be-used-as-the-callback-url
    }
}
